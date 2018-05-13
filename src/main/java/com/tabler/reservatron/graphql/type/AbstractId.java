package com.tabler.reservatron.graphql.type;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLNonNull;

import java.util.Base64;

public abstract class AbstractId {
    protected final String id;

    public AbstractId(Class<?> clazz, String... localId) {
        this.id = encodeId(clazz, localId);
    }

    @GraphQLField
    public @GraphQLNonNull String id() {
        return id;
    }

    public static String encodeId(Class clazz, String... localId) {
        String id = String.join("/", localId);
        return Base64.getEncoder().encodeToString((clazz.getName() + "/" + id).getBytes());
    }

    public static String[] decodeId(String id) {
        return new String(Base64.getDecoder().decode(id)).split("/");
    }
}
