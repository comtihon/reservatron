package com.tabler.reservatron.graphql.type;

import graphql.TypeResolutionEnvironment;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class ClassTypeResolver implements TypeResolver {
    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        return GraphQLAnnotations.object(env.getObject().getClass());
    }
}
