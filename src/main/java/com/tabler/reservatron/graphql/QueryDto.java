package com.tabler.reservatron.graphql;

import com.tabler.reservatron.entity.Desk;
import com.tabler.reservatron.graphql.dto.ReservationDto;
import com.tabler.reservatron.graphql.dto.TableDto;
import com.tabler.reservatron.graphql.type.Node;
import com.tabler.reservatron.graphql.type.Page;
import com.tabler.reservatron.repository.DeskRepositoryService;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.connection.AbstractPaginatedData;
import graphql.annotations.connection.GraphQLConnection;
import graphql.annotations.connection.PaginatedData;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static com.tabler.reservatron.graphql.type.AbstractId.decodeId;

@Slf4j
@GraphQLName("Query")
public class QueryDto {
    @GraphQLField
    @GraphQLConnection
    @GraphQLName("allTables")
    public static PaginatedData<TableDto> getAllTables(DataFetchingEnvironment environment) {
        ApplicationContext context = environment.getContext();
        DeskRepositoryService repositoryService = context.getBean(DeskRepositoryService.class);
        Page page = new Page(environment);
        List<Desk> allDesks;
        if(page.applyPagination()) {
            allDesks = repositoryService.findAll();
        } else {
            allDesks = repositoryService.findAll();
        }
        List<TableDto> tables = allDesks.stream().map(TableDto::new).collect(Collectors.toList());
        return new AbstractPaginatedData<TableDto>(false, false, tables) {
            @Override
            public String getCursor(TableDto entity) {
                return entity.id();
            }
        };
    }

    @GraphQLField
    public static Node node(DataFetchingEnvironment environment, @GraphQLNonNull String id) {
        String[] decoded = decodeId(id);
        if (decoded[0].equals(TableDto.class.getName()))
            return TableDto.getById(environment, decoded[1]);
        if (decoded[0].equals(ReservationDto.class.getName()))
            return ReservationDto.getById(environment, decoded[1]);
        log.error("Don't know how to get {}", decoded[0]);
        throw new RuntimeException("Don't know how to get " + decoded[0]);
    }
}
