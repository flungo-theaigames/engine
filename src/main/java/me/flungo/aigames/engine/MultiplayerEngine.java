/*
 * Copyright 2015 theaigames.com <developers@theaigames.com>,
 * Copyright 2015 Fabrizio Lungo <fab@lungo.co.uk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.flungo.aigames.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.flungo.aigames.engine.api.Engine;
import me.flungo.aigames.engine.api.Logic;
import me.flungo.aigames.engine.api.io.BotCommunication;
import me.flungo.aigames.engine.api.io.Player;
import me.flungo.aigames.engine.io.IOPlayer;

/**
 * Engine class
 *
 * A general engine to implement IO for bot classes All game logic is handled by
 * implemented Logic interfaces.
 *
 * @author Jackie Xu <jackie@starapple.nl>, Jim van Eeden <jim@starapple.nl>
 */
public class MultiplayerEngine implements Engine, BotCommunication {

    // Boolean representing current engine running state
    private boolean isRunning;

    // Class implementing Logic interface; handles all data
    private Logic logic;

    // ArrayList containing player handlers
    private ArrayList<Player> players;

    // Engine constructor
    public MultiplayerEngine() {
        this.isRunning = false;
        this.players = new ArrayList<>();
    }

    // Sets game logic
    @Override
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public boolean hasEnded() {
        return this.logic.isGameWon();
    }

    @Override
    // Adds a player to the game
    public void addPlayer(String command) throws IOException {

        // Create new process
        Process process = Runtime.getRuntime().exec(command);

        // Attach IO to process
        IOPlayer player = new IOPlayer(process);

        // Add player
        this.players.add(player);

        // Start running
        player.run();
    }

    @Override
    // Method to start engine
    public void start() throws Exception {

        int round = 0;

        // Set engine to running
        this.isRunning = true;

        // Set up game settings
        this.logic.setupGame(this.players);

        // Keep running
        while (this.isRunning) {

            round++;

            // Play a round
            this.logic.playRound(round);

            // Check if win condition has been met
            if (this.hasEnded()) {

                System.out.println("stopping...");

                // Stop running
                this.isRunning = false;

                // Close off everything
                try {
                    this.logic.finish();
                } catch (Exception ex) {
                    System.out.println(ex);
                    Logger.getLogger(MultiplayerEngine.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

}
