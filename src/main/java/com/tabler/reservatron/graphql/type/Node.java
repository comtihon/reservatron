package com.tabler.reservatron.graphql.type;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.annotationTypes.GraphQLTypeResolver;

@GraphQLTypeResolver(ClassTypeResolver.class)
public interface Node {
    @GraphQLField
    @GraphQLNonNull
    String id();
}

