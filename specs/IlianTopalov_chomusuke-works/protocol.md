# Connection establishment
Following the establishment of the TCP connection, a welcome message is sent by the server.
It is always preceded by a 32-bit integer representing the number of lines in the message.

# Supported operations

The server responds to the client's queries by simply sending the result of the computation requested,
or an error code if the prompt syntax is incorrect.

## Addition
`ADD <A> <B>`

## Subtraction
`SUB <A> <B>` 

## Multiplication
`MUL <A> <B>`

## Closing the connection
`BYE`

# Error codes
`EUO` = `Error: Unknown Operation` The given operation code is unknown

`ENF` = `Error: Number Format` - The number is in incorrect format