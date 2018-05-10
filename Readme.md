# Transaction calculator service  [![Build Status](https://travis-ci.org/comtihon/reservatron.svg?branch=master)](https://travis-ci.org/comtihon/reservatron)


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
## Make a reservation for a table

Endpoint: `/api/v1/table/{id}/reservation`

### Request

Method: `POST`

Body:

```
{
  "customer_name": "Mr. Smith",
  "timeslot": {
    "from": "2018-01-04T18:00:00.000+00:00",
    "to": "2018-01-04T20:00:00.000+00:00"
  }
}
```

### Response

Return the response status depending whether the reservation was successful or not.
Errors:
`CONFLICT` - table occupied at selected time;
`NO_SUCH_TABLE` - no table for selected id;

## Show all reservations for a table

Endpoint: `/api/v1/table/{id}`

### Request

Method: `GET`

### Response

Body:
```
{
  "id": 0,
  "name": "Table with a view to the mountains",
  "reservations": [
    {
      "customer_name": "Mr. Smith",
      "from": "2018-01-04T18:00:00",
      "to": "2018-01-04T20:00:00"
    },
    {
      "customer_name": "Mr. Pink",
      "from": "2018-01-04T20:00:00",
      "to": "2018-01-04T21:00:00"
    }
  ]
}
```