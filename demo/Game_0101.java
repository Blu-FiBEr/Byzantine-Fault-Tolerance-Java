package demo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import common.Game;
import common.Machine;

public class Game_0101 extends Game {
	private ArrayList<Machine> list_of_machines = new ArrayList<>();
	private int total_faulty;

	@Override
	public void addMachines(ArrayList<Machine> list_of_machines, int numFaulty) {
		this.list_of_machines = list_of_machines;
		this.total_faulty = numFaulty;

		for(Machine mach: this.list_of_machines)
			mach.setMachines(this.list_of_machines);
		
	}

	public void startPhase(int leaderId, ArrayList<Boolean> areCorrect)
	{

		for(Machine m:list_of_machines)
		{
			m.setState(areCorrect.get(list_of_machines.indexOf(m)));
		}
		list_of_machines.get(leaderId).setLeader();


	}
	@Override
	public void startPhase() {
		HashMap<Integer, Integer> random_map = new HashMap<>();
		this.total_faulty = new Random().nextInt(this.total_faulty+1);
		
		for (int i = 0; i < this.total_faulty; i++) {
			int random_faulty = (int) (Math.random() * (list_of_machines.size()));
			while (random_map.containsKey(random_faulty)) {
				random_faulty = (int) (Math.random() * (list_of_machines.size()));
			}
			random_map.put(random_faulty, 1);
		}

		for (int i = 0; i < this.list_of_machines.size(); i++) {
			if (random_map.containsKey(i)) {
				this.list_of_machines.get(i).setState(false);
			}
			else {
				this.list_of_machines.get(i).setState(true);
			}
		}

		int random_leader = (int) (Math.random() * (list_of_machines.size()));
		this.list_of_machines.get(random_leader).setLeader();
	}
}