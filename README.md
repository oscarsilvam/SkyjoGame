 # :video_game: Skyjo - Java Implementation

This project is a Java implementation of the **Skyjo** card game. 

It follows  an **MVC architecture** and uses a **Command design pattern** to 
manage game actions.

## Features

- Full playable Skyjo game
- MVC architecture (Model / View / Controller)
- Command pattern for game actions
- Dynamic grids (rows/columns can be reduced)
- Automated tests with coverage (JaCoCo)
- Continuous Integration with GitHub CI

## Download & Play
Download the executable JAR (includes all dependencies)
:point_right: http://oscarsilvam.github.io/SkyjoGame/jar/Skyjo-jar-with-dependencies.jar
### Run the game:
The game is started from the command line and requires player names as arguments
The game supports between **2 and 4 players**

### Example
```bash
java -jar Skyjo-jar-with-dependencies.jar Oscar Luc
```

## :notebook: Documentation
The full API documentation is available here:
https://oscarsilvam.github.io/SkyjoGame/javadoc/
## How to Play
The goal of the game is to **have the lowest score** at the end,

## Game Setup
- Each player has a gird of cards (face down)
- Two cards are revealed at hte start
- A draw pile and a discard pile are created

## :arrows_clockwise: Turn Overview
Each turn consists of:

1. **Draw a card**
   - From the deck (goes to buffer)
   - Or form the discard pile

2. **Choose an action**
    - Replace on fo your cards with the drawn card
    - Or discard the drawn card and reveal on fo your cards

3. **Optional: Reduce**
    - If a full row or column has identical revealed values
    - It is removed and sent tho the discard pile

## :checkered_flag: End of the Game
- When a player reveals all their cards -> they become the **game ender**
- Other players play one final turn
- The game ends when the turn comes back to the game ender

## :clipboard: Scoring
- The score is the sum of all revealed cards
- The player with the **lowest score wins**

## Projet Structure 
```tree
.
└── skyjo
    ├── controller
    ├── model
    └── view
```

## Authors
- Oscar Silva
- Luc St-Germain
- Maximilian Schiedermeier





