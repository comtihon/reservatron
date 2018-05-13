package com.tabler.reservatron.graphql.dto;

import com.tabler.reservatron.entity.Desk;
import com.tabler.reservatron.entity.Reservation;
import com.tabler.reservatron.graphql.type.AbstractId;
import com.tabler.reservatron.graphql.type.Node;
import com.tabler.reservatron.repository.DeskRepositoryService;
import com.tabler.reservatron.repository.ReservationRepositoryService;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import graphql.annotations.connection.AbstractPaginatedData;
import graphql.annotations.connection.GraphQLConnection;
import graphql.annotations.connection.PaginatedData;
import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@GraphQLName("Table")
public class TableDto extends AbstractId implements Node {
    @GraphQLNonNull
    @GraphQLField
    private Long tableId;
    @GraphQLNonNull
    @GraphQLField
    private String name;

    public TableDto(Desk desk) {
        super(TableDto.class, desk.getDeskId().toString());
        this.tableId = desk.getDeskId();
        this.name = desk.getName();
    }

    @GraphQLField
    @GraphQLConnection
    public PaginatedData<ReservationDto> reservations(DataFetchingEnvironment environment) {
        ApplicationContext context = environment.getContext();
        ReservationRepositoryService repositoryService = context.getBean(ReservationRepositoryService.class);
        List<Reservation> reservations = repositoryService.findByTable(tableId);
//        TODO pagination here
        List<ReservationDto> tables = reservations.stream().map(ReservationDto::new).collect(Collectors.toList());
        return new AbstractPaginatedData<ReservationDto>(false, false, tables) {
            @Override
            public String getCursor(ReservationDto entity) {
                return entity.id();
            }
        };
    }

    public static TableDto getById(DataFetchingEnvironment environment, String id) {
        ApplicationContext context = environment.getContext();
        DeskRepositoryService repositoryService = context.getBean(DeskRepositoryService.class);
        return repositoryService.findById(Long.valueOf(id)).map(TableDto::new).orElse(null);
    }
}
