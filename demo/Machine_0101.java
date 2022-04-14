package demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import common.Location;
import common.Machine;

public class Machine_0101 extends Machine {

    public void setMachines(ArrayList<Machine> Machines) {
        this.machines = Machines;
        this.id = machines.indexOf(this);
        this.t = (int) (Machines.size() / 3);
        if (this.t == ((double) Machines.size()) / 3) {
            this.t--;
        }
        this.shuffled.addAll(Machines);
    }

    public Machine_0101() {
        
        this.machines = new ArrayList<Machine>();
        this.shuffled = new ArrayList<Machine>();
    }

    @Override
    public void setStepSize(int stepSize) {
        step = stepSize;
    }

    @Override
    public void setState(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    @Override
    public void setLeader() {
        // Starting with a random decision
        Random random = new Random();
        this.curr_decision = (random.nextInt(2));
        int curr_phase = this.phasenum;
        if (isCorrect) {
            for (Machine machine : this.machines) {
                machine.sendMessage(this.id, curr_phase, 0, this.curr_decision);
            }
        } else {
            // Shuffle the list of machines
            Collections.shuffle(shuffled);
            Random r1 = new Random();

            // Number of machines to which Leader wont send a msg
            int x = r1.nextInt(t + 1);

            /**
             * For Testing
             * 
             * System.out.println(x);
             */

            int dummy_decision = (r1.nextInt(2));
            // Send machine to all machines except first x machines from the shuffled list
            for (int i = x; i < this.shuffled.size(); i++) {
                this.shuffled.get(i).sendMessage(this.id, curr_phase, 0, dummy_decision);
            }
        }
    }

    @Override
    public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) {
        /**
         * For Testing
         * 
         * System.out.println("msg " + sourceId + " to " + this.id + " " + roundNum + "
         * " + phaseNum + " " + this.phasenum);
         */

        // If the machine is correct
        if (isCorrect) {
            // The machine will act only on the messages relevant to it
            if (this.phasenum == phaseNum && this.roundnum <= roundNum) {
                // If machine gets a msg from the leader
                if (roundNum == 0) {
                    this.roundnum = 1;
                    for (Machine machine : this.machines) {
                        machine.sendMessage(this.id, phaseNum, roundNum + 1, decision);
                    }
                }
                // If machine gets a round 1 msg
                else if (roundNum == 1) {
                    this.round1_count++;
                    if (decision == 0) {
                        this.round1left_count++;
                    } else {
                        this.round1right_count++;
                    }

                    // If enough round1 messages are received
                    if (round1_count == 2 * t + 1) {

                        // If enough round1 messages are received but the current round is not round1
                        if (this.roundnum == 0) {
                            this.roundnum = 2;
                            // The machine has to send the round1 messages it was supposed to send
                            for (Machine machine : this.machines) {
                                machine.sendMessage(this.id, phaseNum, 1,
                                        ((round1left_count > round1right_count) ? 0 : 1));
                            }
                        } else {
                            this.roundnum = 2;
                        }
                        // Sending round2 messages
                        for (Machine machine : this.machines) {
                            machine.sendMessage(this.id, phaseNum, 2, ((round1left_count > round1right_count) ? 0 : 1));
                        }
                    }
                }

                // If a round2 message is received
                else if (roundNum == 2) {

                    this.round2_count++;
                    if (decision == 0) {
                        this.round2left_count++;
                    } else {
                        this.round2right_count++;
                    }

                    // If enough round2 messages are identical
                    if (round2left_count == 2 * t + 1 || round2right_count == 2 * t + 1) {
                        this.phasenum++;

                        // If enough round2 identical msg received but the machine is still in round0
                        if (this.roundnum == 0) {
                            this.roundnum = 3;

                            // Sending the messages that it was supposed to send
                            for (Machine machine : this.machines) {
                                machine.sendMessage(this.id, phaseNum, 1, ((round2left_count == 2 * t + 1) ? 0 : 1));
                            }
                            for (Machine machine : this.machines) {
                                machine.sendMessage(this.id, phaseNum, 2, ((round2left_count == 2 * t + 1) ? 0 : 1));
                            }
                        }
                        // If enough round2 identical msg received but the machine is still in round1
                        else if (this.roundnum == 1) {
                            this.roundnum = 3;
                            // Sending the messages that it was supposed to send
                            for (Machine machine : this.machines) {
                                machine.sendMessage(this.id, phaseNum, 2, ((round2left_count == 2 * t + 1) ? 0 : 1));
                            }
                        } else {
                            this.roundnum = 3;
                        }

                        // Final decision and change of direction
                        if (round2left_count == 2 * t + 1) {
                            /**
                             * Matrix for left turn:
                             * 
                             * |0    1|
                             * |      |
                             * |-1   0|
                            */
                            this.dir = new Location(-1 * this.dir.getY(), this.dir.getX());
                        } else {
                            /**
                             * Matrix for rigth turn:
                             * 
                             * |0  -1|
                             * |     |
                             * |1   0|
                             */
                            this.dir = new Location(this.dir.getY(), -1 * this.dir.getX());
                        }

                        //Reinitialize required data members
                        this.round1_count = 0;
                        this.round1left_count = 0;
                        this.round1right_count = 0;
                        this.round2_count = 0;
                        this.round2left_count = 0;
                        this.round2right_count = 0;
                        this.roundnum = 0;
                    }

                }
            }
            //If other machines have moved on to the next phase, it means that we have to flag an error
            else if (this.phasenum < phaseNum) {
                System.out.println("ERROR");
                this.phasenum = phaseNum;
                this.round1_count = 0;
                this.round1left_count = 0;
                this.round1right_count = 0;
                this.round2_count = 0;
                this.round2left_count = 0;
                this.round2right_count = 0;
                this.roundnum = 0;
                this.sendMessage(sourceId, phaseNum, roundNum, decision);
            }
        }
        //The code will be similar for faulty machine, except that it can choose to remain silent or send the same incorrect msg to all machines
        else {
            if (this.phasenum == phaseNum && this.roundnum <= roundNum) {
                if (roundNum == 0) {
                    this.roundnum = 1;
                    int decn = new Random().nextInt(2);
                    for (Machine machine : this.machines) {
                        if (new Random().nextInt(2) == 0) {
                            machine.sendMessage(this.id, phaseNum, roundNum + 1, decn);
                        }

                    }
                } else if (roundNum == 1) {

                    this.round1_count++;
                    if (decision == 0) {
                        this.round1left_count++;
                    } else {
                        this.round1right_count++;
                    }
                    if (round1_count == 2 * t + 1) {
                        this.roundnum = 2;
                        int decn = new Random().nextInt(2);
                        if (new Random().nextInt(2) == 0) {
                            for (Machine machine : this.machines) {
                                machine.sendMessage(this.id, phaseNum, roundNum + 1, decn);
                            }
                        }
                    }
                } else if (roundNum == 2) {

                    this.round2_count++;
                    if (decision == 0) {
                        this.round2left_count++;
                    } else {
                        this.round2right_count++;
                    }
                    if (round2left_count == 2 * t + 1 || round2right_count == 2 * t + 1) {

                        this.phasenum++;
                        if (this.roundnum == 0) {
                            this.roundnum = 3;
                            if (new Random().nextInt(2) == 0) {
                                for (Machine machine : this.machines) {
                                    machine.sendMessage(this.id, phaseNum, 1,
                                            ((round2left_count == 2 * t + 1) ? 0 : 1));
                                }
                            }
                            if (new Random().nextInt(2) == 0) {
                                for (Machine machine : this.machines) {
                                    machine.sendMessage(this.id, phaseNum, 2,
                                            ((round2left_count == 2 * t + 1) ? 0 : 1));
                                }
                            }
                        } else {
                            this.roundnum = 3;
                        }

                        int r = new Random().nextInt(3);
                        if (r == 0) {
                            this.dir = new Location(-1 * this.dir.getY(), this.dir.getX());
                        } else if (r == 1) {
                            this.dir = new Location(this.dir.getY(), -1 * this.dir.getX());
                        }

                        this.round1_count = 0;
                        this.round1left_count = 0;
                        this.round1right_count = 0;
                        this.round2_count = 0;
                        this.round2left_count = 0;
                        this.round2right_count = 0;
                        this.roundnum = 0;

                    }
                }
            }
            else if (this.phasenum < phaseNum) {
                System.out.println("ERROR");
                this.phasenum = phaseNum;
                this.round1_count = 0;
                this.round1left_count = 0;
                this.round1right_count = 0;
                this.round2_count = 0;
                this.round2left_count = 0;
                this.round2right_count = 0;
                this.roundnum = 0;
                this.sendMessage(sourceId, phaseNum, roundNum, decision);
            }
        }
    }

    @Override
    public synchronized void move() {
        pos.setLoc(pos.getX() + dir.getX() * step, pos.getY() + dir.getY() * step);

    }

    @Override
    public String name() {
        return "0101_" + id;
    }

    @Override
    public Location getPosition() {

        return new Location(pos.getX(), pos.getY());
    }

    private int step;
    private Location pos = new Location(0, 0);
    private Location dir = new Location(0, 1); // using Location as a 2d vector. Bad!
    private static int nextId = 0;  //Only for testing
    private int id;
    private int t;  //Valur of max possible faulty machines
    private ArrayList<Machine> machines;
    private ArrayList<Machine> shuffled;
    private int phasenum = 0;   //Current phasenumber of the machine
    private int roundnum = 0;   //Current Roundnumber of the machine
    private boolean isCorrect;
    private int curr_decision;  //First decision of a leader machine
    private int round1_count = 0, round2_count = 0, round1left_count = 0, round1right_count = 0, round2left_count = 0,
            round2right_count = 0;


    //For testing
    public int GetId() {
        return id;
    }
    public int getPhasenum() {
        return phasenum;
    }
    public int getRoundnum() {
        return roundnum;
    }
    public void setRoundnum(int roundnum) {
        this.roundnum = roundnum;
    }
    //
}
