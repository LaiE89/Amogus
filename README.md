# Multiplayer Tetris
Group Project for CSC207 at the University of Toronto

This project is heavily inspired by Nintendo's Tetris 99 battle royale style Tetris.

## Demo Video
[![Demo Video](https://img.youtube.com/vi/HLOUq4jEC1g/0.jpg)](https://youtu.be/HLOUq4jEC1g)

## Features
- 2 to 5 player multiplayer compatibility through peer-to-peer hosting and TCP connections
- Lobby chat system
- Garbage blocks mechanic
- Hold blocks mechanic
- Basic Tetris mechanics
- Versatile settings to improve accessibility
- Adjustable user interface and audio

## Design Patterns
- Singleton: Used to provide a global reference for audio and GUI views.
- Command: Provides flexibility on how controls are activated.
- Object Pool: Used to prevent creating and destroying Tetris blocks during runtime.
- Factory: Provides a common interface for generating Tetris blocks.

## Installation
Click [here](https://www.dropbox.com/s/wlqu5pt92fh5vze/Tetris69.exe?dl=1) to install the executable file. The suggested JRE version is 18.0.2.
