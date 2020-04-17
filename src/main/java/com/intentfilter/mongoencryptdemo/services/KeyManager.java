package com.intentfilter.mongoencryptdemo.services;

import com.mongodb.ClientEncryptionSettings;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.intentfilter.mongoencryptdemo.services.KeyManager.DbStrings.*;

@Service
public class KeyManager {

    public Map<String, Map<String, Object>> getKmsProviders() throws IOException {
        byte[] localMasterKey = new byte[96];

        try (FileInputStream fis = new FileInputStream(MASTER_KEY_FILE_PATH)) {
            fis.readNBytes(localMasterKey, 0, 96);
        }

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put(KMS_PROVIDER_NAME, keyMap);
        return kmsProviders;
    }

    public LocalKey generateLocalKeyId(String keyVaultNamespace, Map<String, Map<String, Object>> kmsProviders) {
        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder().build())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();

        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);
        BsonBinary dataKeyId = clientEncryption.createDataKey(KMS_PROVIDER_NAME, new DataKeyOptions());
        String base64DataKeyId = Base64.getEncoder().encodeToString(dataKeyId.getData());

        return new LocalKey(dataKeyId.asUuid(), base64DataKeyId);
    }

    public void writeMasterKey() throws IOException {
        byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);

        try (FileOutputStream stream = new FileOutputStream(MASTER_KEY_FILE_PATH)) {
            stream.write(localMasterKey);
        }
    }

    public Document getKeyDocumentFromVault(String base64KeyId) {
        MongoCollection<Document> collection = MongoClients.create().getDatabase(KEY_VAULT_DB).getCollection(KEY_VAULT_COLLECTION);
        Bson query = Filters.eq("_id", new Binary((byte) 4, Base64.getDecoder().decode(base64KeyId)));
        return collection.find(query).first();
    }

    public static class LocalKey {
        public final UUID uuidKeyId;
        public final String base64KeyId;

        public LocalKey(UUID uuidKeyId, String base64KeyId) {
            this.uuidKeyId = uuidKeyId;
            this.base64KeyId = base64KeyId;
        }
    }

    public interface DbStrings {
        String KEY_VAULT_DB = "demo";
        String KEY_VAULT_COLLECTION = "__keyVault";
        String MASTER_KEY_FILE_PATH = "master-key.txt";
        String KMS_PROVIDER_NAME = "local";
    }
}
