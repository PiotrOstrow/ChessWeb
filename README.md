# Start the server

```
mvn spring-boot:run
```

# Endpoints

## Auth

#### Log in

`POST /auth/login/`

```json
{
  "username": "user123",
  "password": "password123"
}
```

## Users

#### Get all users

`GET /users/`

#### Get specific user

`GET /users/{username}/`

#### Create user

`POST /users/`

```json
{
  "username": "user123",
  "password": "password123",
  "email": "user123@email.com"
}
```

#### Delete user

`DELETE /users/{username}/`

## Games

#### Get all games played

`GET /games/`

#### Get all games played of a specific player

`GET /games/{username}/`

## Puzzles

#### Get all puzzles

`GET /puzzles/`

#### Create a puzzle

`POST /puzzles/`

```json
{
  "fen": "7R/p1r5/6k1/6p1/P5Kp/5P1P/8/8 w - - 4 44",
  "moves": [
    "h8a8",
    "c7c4",
    "f3f4",
    "c4f4"
  ],
  "rating": "password123",
  "themes": [
    "endgame",
    "mate",
    "mateIn2",
    "rookEndgame",
    "short"
  ]
}
```

#### Get a specific puzzle

`GET /puzzles/{id}/`

#### Delete a puzzle

`DELETE /puzzles/{id}/`

#### Get a random puzzle to play at your puzzle rating

`GET /puzzles/random/`

#### Submit a solution to a puzzle

`POST /puzzles/solve/`

```json
{
  "id": "5",
  "moves": [
    "h8a8",
    "c7c4",
    "f3f4",
    "c4f4"
  ],
  "time": "0"
}
```
