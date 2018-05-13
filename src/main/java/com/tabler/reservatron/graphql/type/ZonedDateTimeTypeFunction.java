package com.tabler.reservatron.graphql.type;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;

import java.lang.reflect.AnnotatedType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeTypeFunction implements TypeFunction {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static GraphQLScalarType instance = new GraphQLScalarType("DateTime", "ZonedDateTime", new Coercing<ZonedDateTime, String>() {
        @Override
        public String serialize(Object dataFetcherResult) {
            return ((ZonedDateTime) dataFetcherResult).format(FORMATTER);
        }

        @Override
        public ZonedDateTime parseValue(Object input) {
            return parse(input);
        }

        @Override
        public ZonedDateTime parseLiteral(Object input) {
            return parse(input);
        }
    });

    @Override
    public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
        return aClass.equals(ZonedDateTime.class);
    }

    @Override
    public GraphQLType buildType(boolean input, Class<?> aClass, AnnotatedType annotatedType, ProcessingElementsContainer container) {
        return instance;
    }

    private static ZonedDateTime parse(Object object) {
        String value;
        if (object.getClass().equals(StringValue.class)) {
            value = ((StringValue) object).getValue();
        } else if (object.getClass().equals(String.class)) {
            value = (String) object;
        } else {
            value = object.toString();
        }
        try {
            return ZonedDateTime.parse(value, FORMATTER);
        } catch (Exception ignored) {
            return null;
        }
    }
}
