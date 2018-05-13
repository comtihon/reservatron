package com.tabler.reservatron.graphql.type;

import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;

public class Page {
    @Getter
    protected Integer first;
    @Getter
    protected Integer last;
    protected String id;

    public Page(DataFetchingEnvironment env) {
        id = env.getArgument("after");
        if (id == null) {
            id = env.getArgument("before");
        }
        first = env.getArgument("first");
        last = env.getArgument("last");
    }

    public String getId() {
        if (id == null) {
            return null;
        }
        return AbstractId.decodeId(id)[1]; // cursor's id is always GraphQL's Node Id
    }

    public boolean applyPagination() {
        return id != null || first != null || last != null;
    }
}
