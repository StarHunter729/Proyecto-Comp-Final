package Objects;

import java.util.ArrayList;
import java.util.List;

public class acknowlegmentTable {
	private int sumAcknowledgemets = 0;
	private int subAcknowledgemets = 0;
	private int mulAcknowledgemets = 0;
	private int divAcknowledgemets = 0;
	
	public List<Connection> sumCells = new ArrayList<Connection>();;
	public List<Connection> subCells = new ArrayList<Connection>();;
	public List<Connection> mulCells = new ArrayList<Connection>();;
	public List<Connection> divCells = new ArrayList<Connection>();;

	public acknowlegmentTable() {}
	public boolean sumList(String cellFingerprint, int num) {
		for (int i = 0; i < sumCells.size(); i++) {
			if(cellFingerprint == sumCells.get(i).getId()) {
				sumCells.get(i).setLastKnown();
				return false;
				}
		}
		Connection con = new Connection(cellFingerprint);
		sumCells.add(con);
		sumAcknowledgemets += num;
		return true;
	}
	public boolean subList(String cellFingerprint, int num) {
		for (int i = 0; i < subCells.size(); i++) {
			if(cellFingerprint == subCells.get(i).getId()) {
				subCells.get(i).setLastKnown();
				return false;
				}
		}
		Connection con = new Connection(cellFingerprint);
		subCells.add(con);
		subAcknowledgemets += num;
		return true;
	}
	public boolean mulList(String cellFingerprint, int num) {
		for (int i = 0; i < mulCells.size(); i++) {
			if(cellFingerprint == mulCells.get(i).getId()) {
				mulCells.get(i).setLastKnown();
				return false;
				}
		}
		Connection con = new Connection(cellFingerprint);
		mulCells.add(con);
		mulAcknowledgemets += num;
		return true;
	}
	public boolean divList(String cellFingerprint, int num) {
		for (int i = 0; i < divCells.size(); i++) {
			if(cellFingerprint == divCells.get(i).getId()) {
				divCells.get(i).setLastKnown();
				return false;
				}
		}
		Connection con = new Connection(cellFingerprint);
		divCells.add(con);
		divAcknowledgemets += num;
		return true;
	}
	
	public int getSumAcknowledgements() {
		return this.sumAcknowledgemets;
	}
	
	public int getSubAcknowledgements() {
		return this.subAcknowledgemets;
	}
	
	public int getMulAcknowledgements() {
		return this.mulAcknowledgemets;
	}
	
	public int getDivAcknowledgements() {
		return this.divAcknowledgemets;
	}

}
