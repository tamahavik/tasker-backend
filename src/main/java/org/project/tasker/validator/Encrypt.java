package org.project.tasker.validator;

import org.project.tasker.utils.EncryptionUtils;

import javax.persistence.AttributeConverter;

public class Encrypt implements AttributeConverter<String, String> {

    private final EncryptionUtils encryptionUtils;

    public Encrypt(EncryptionUtils encryptionUtils) {
        this.encryptionUtils = encryptionUtils;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionUtils.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionUtils.decrypt(dbData);
    }
}
