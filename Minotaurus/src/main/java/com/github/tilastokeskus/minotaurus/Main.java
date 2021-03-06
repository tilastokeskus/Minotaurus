/*
 * The MIT License
 *
 * Copyright 2016 Olavi Mustanoja.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.tilastokeskus.minotaurus;

import com.github.tilastokeskus.minotaurus.maze.MazeGenerator;
import com.github.tilastokeskus.minotaurus.scenario.Scenario;
import com.github.tilastokeskus.minotaurus.simulation.SimulationHandler;
import com.github.tilastokeskus.minotaurus.ui.MainWindow;
import com.github.tilastokeskus.minotaurus.ui.SimulationWindow;
import java.util.List;
import com.github.tilastokeskus.minotaurus.runner.Runner;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        showMainWindow();
    }
    
    private static void showMainWindow() {
        MainWindow window = new MainWindow();
        window.show();
    }
    
    public static void startSimulation(MazeGenerator gen, Scenario scenario,
            List<Runner> runners, int rate, int cap) {
        SimulationHandler simHandler = new SimulationHandler(gen, scenario, runners);
        SimulationWindow mazeWindow = new SimulationWindow(simHandler);
        mazeWindow.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                simHandler.stop();
            }
        });
        simHandler.addObserver(mazeWindow);
        mazeWindow.show();
        simHandler.startSimulation(rate, cap);
    }
    
}
