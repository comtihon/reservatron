# Reservatron  [![Build Status](https://travis-ci.org/comtihon/reservatron.svg?branch=master)](https://travis-ci.org/comtihon/reservatron)
Reserve table service.
Use redis distributed lock to guarantee reliable reservations even when
multiple instances of Reservatron are running.
This service uses GraphQL API via [this](https://github.com/graphql-java/graphql-java-annotations)
library.

## Future improvements:
* pagination. Easy way will be integrate querydsl and
compose queries in repositories to support pagination on DB level
* filtering. Also querydsl could be used to optimise
requests like `getAllTables(filter: {tableId: 1})`
* unsubscribe subscriptions on connection close (due to the strange bug
subscriber's onConnect is not called)
* subscriptions in case of multiple instances. Need to send events with reservations
to Kafka and publish updates for subscribed clients.

### In docker
Ensure you've built [Admin](https://github.com/comtihon/tabler) before.

    sudo ./gradlew build docker -x test -x test_integration
    sudo docker-compose up -d

### In OS

    export POSTGRES_HOST=localhost
    export REDIS_HOST=localhost
    ./gradlew bootRun

## Testing

    ./gradlew check

## Protocol
Graphql is used for communication. Make introspective query to get schema
for details.
Endpoint for query and mutations is `/graphql`, for subscriptions is `/subscriptions`

### Examples:
#### Make a reservation for a table

Method: `POST`

Body:

```
mutation ReserveTable
{   reserveTable(input: {tableId: 1,
                         guest: "Test",
                         from: "2018-01-04T18:00:00.000+0000",
                         to: "2018-01-04T19:00:00.000+0000",
                         clientMutationId: "uuid"})
    {status}
}
```

Response:

```
{
    "data": {
        "reserveTable": {
            "status": "CONFLICT"
        }
    },
    "errors": [],
    "extensions": null
}
```

#### Show all reservations for all tables

Method: `POST`

Body:
```
query {allTables
        {edges {node {name reservations
                              {edges {node {guest from to}}}
                     }
               }
        }
      }
```

Response:

```
{
    "data": {
        "allTables": {
            "edges": [
                {
                    "node": {
                        "name": "test",
                        "reservations": {
                            "edges": [
                                {
                                    "node": {
                                        "guest": "Test",
                                        "from": "2018-01-04T18:00:00.000+0000",
                                        "to": "2018-01-04T19:00:00.000+0000"
                                    }
                                }
                            ]
                        }
                    }
                },
                {
                    "node": {
                        "name": "test2",
                        "reservations": {
                            "edges": []
                        }
                    }
                }
            ]
        }
    },
    "errors": [],
    "extensions": null
}
```

#### Subscribe for table reservations

`Websocket`

Body:
```
subscription RealTimeReservationsSubscription {
               newReservationMade(tableId: 1) {
                 guest
                 from
                 to
               }
             }
```
On every new subscription to this server you will receive a `Reservation`.
__Important!__ This repo is just an example of graphql usage. With multiple
instances of this service subscriptions won't work correctly, as one instance
doesn't receive events about subscription has been made from the other.
__Important!__ Subscriptions unsubscribe feature is not working now.
`CompletionStageMappingPublisher`'s `onSubscribe` is not called, thus link
between `GraphqlSubscriber` and `Subscription` can't be made. As a result,
when connection is closed we can't cancel subscription.