package com.intentfilter.mongoencryptdemo.config;

import com.intentfilter.mongoencryptdemo.models.Person;
import com.intentfilter.mongoencryptdemo.services.KeyManager;
import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.BsonDocument;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import java.io.IOException;
import java.util.Map;

import static com.intentfilter.mongoencryptdemo.services.KeyManager.DbStrings.*;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static java.util.UUID.fromString;
import static org.bson.UuidRepresentation.STANDARD;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.springframework.data.mongodb.core.schema.JsonSchemaProperty.encrypted;
import static org.springframework.data.mongodb.core.schema.JsonSchemaProperty.string;

@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String mongoConnectionString;

    private KeyManager keyManager;
    private static final String KEY_VAULT_NAMESPACE = KEY_VAULT_DB + "." + KEY_VAULT_COLLECTION;
    private static final String PERSON_COLLECTION_NAMESPACE = REGULAR_DB + "." + Person.Fields.CollectionName;

    public MongoConfig(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public @Bean
    MongoClient mongoClient() throws IOException {
        // Uncomment below lines to generate a new master key stored in local filesystem and LocalKey using master key for first time

        // Write master key to a file in local filesystem
        // keyManager.writeMasterKey();

        final Map<String, Map<String, Object>> kmsProviders = keyManager.getKmsProviders();

        // Generate a local key using master key, this gets saved into local database
        // final KeyManager.LocalKey localKey = keyManager.generateLocalKeyId(KEY_VAULT_NAMESPACE, kmsProviders, mongoConnectionString);

        // Use the local key generated here
        KeyManager.LocalKey generatedLocalKey = new KeyManager.LocalKey(fromString("45c545d9-67be-4e1b-b1be-2f8d7e102a61"), null);

        final CodecRegistry codecRegistry = fromRegistries(fromCodecs(new UuidCodec(STANDARD)), getDefaultCodecRegistry());
        final Map<String, BsonDocument> schemaMap = getSchemaMap(generatedLocalKey, codecRegistry);

        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(KEY_VAULT_NAMESPACE)
                .kmsProviders(kmsProviders)
                .schemaMap(schemaMap)
                .build();

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionString))
                .codecRegistry(codecRegistry)
                .uuidRepresentation(STANDARD)
                .autoEncryptionSettings(autoEncryptionSettings)
                .build();

        return MongoClients.create(clientSettings);
    }

    private Map<String, BsonDocument> getSchemaMap(KeyManager.LocalKey localKey, CodecRegistry codecRegistry) {
        final MongoJsonSchema schema = MongoJsonSchema.builder()
                .property(encrypted(string(Person.Fields.Pan))
                        .aead_aes_256_cbc_hmac_sha_512_deterministic()
                        .keys(localKey.uuidKeyId))
                .build();
        final BsonDocument document = schema.toDocument().toBsonDocument(BsonDocument.class, codecRegistry).getDocument("$jsonSchema");
        return Map.of(PERSON_COLLECTION_NAMESPACE, document);
    }
}
