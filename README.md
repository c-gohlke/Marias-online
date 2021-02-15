# Marias Online

## Setup:

### 1) Start the server 
follow guidelines in README.md of marias-server repository

### 2) Start emulator(s)
Optional: on a browser, go to http://127.0.0.1:8000. This will summarize all the websocket interactions between server and client, and helps keep track of the game flow. If you're browser is already on this link, don't forget to refresh.

### 3) Make sure all emulators have finished loading the app and you have refreshed the browser.

### 4) Create game on one of the emulators

### 5) Add users:

#### Option a) Play alone against bots
Press "Start Game" (will add bots automatically untill 3 players are in the game).

#### Option b) Play with one or 2 friends
To add bot, press "add bot"
To add a "real" player, go on other emulator, press join game, press join.
Note: The player that created the game will be player 1 (dealer). The player that joins first will be player 2 (cuts deck, chooses trump, talon, plays alone and is first to play in the first hand).

### 6) Press "Start Game" on the emulator that created the game
Upon game start, if there wasn't a total of 3 players, the game will be filled with bots.

## Terminology:

- Players:
    - `Forhont` - player who starts each `Round` (differs for `Small` or `Big`)
    - `Player0` - player who is suggesting a game (in `Choosen` Marias `Player0` is identical to `Forhont`)
    - `Player1` - player next to `Player0`, clockwise
    - `Player2` - player next to `Player1`, clockwise

- Cards:
    - `Trumph` - card chosen by `Player0` to set a `trumph` colour
    - `trumph` - colour of cards choosen to be upper than other coulours (applicable only in `Colour` `Set`)
    - `Report` - `Queen` and `King` pair of the same colour (player has to hold both cards), `Queen` has to be played first out of this two cards, because of playing this player, who played, gets +20 points (if `Report` is in `trumph` then +40 points).
    - Colours:
        - `Nuts`
        - `Bells`
        - `Leafs`
        - `Hearts`

- Game hierarchy:
    - `Game` - one session of three players
    - `Rules` - different type of game (usualy same for the `Game` time being)
        - `Chosen` - `Forhont` chooses the `Set` at the beginning of the `Round`
        - `Bid` - Players bid aginst themselfs for the highest `Play` one of them can play (`Small` and `Big` are placed as third and second highest `Plays`).
    - `Round` - a set of ten `Hands`
    - `Set` - one `Round` in the `Game` at given `Rules`
        - `Colour` - player has to gain points and win `Hands` defined in a `Play`
        - `Small` - player has to lose all `Hands`
        - `Big` - player has to win all `Hands`
    - `Play` - final goal in one `Round` (only for `Colour`)
        - `Seven` - `Player0` has to finish game winning last `Hand` with card `Seven of trumph`
        - `Better Seven` - same as `Seven` but `trumph` has to be `Hearts`
        - `A hundred` - `Player0` has to get at least 100 points
        - `Hundred and Seven` - combination of `A hundred` and `Seven`
        - `Better hundred` - `A hundred` played in `Hearts`
        - `Better Hundred and Seven` - combination of `A hundred` and `Seven` played in `Hearts`
        - `Double Seven` - `Player0` has to finish game winning last `Hand` with card `Seven of trumph` and the `Hand` before the last one with `Seven` of prior (at the beginning) set colour
    - `Hand` - one card excahnge from all players
