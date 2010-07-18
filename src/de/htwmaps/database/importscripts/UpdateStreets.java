import java.awt.Color;
import java.util.ArrayList;


public class KnightJumping {
	private Knight knight;
	private Node root;
	private int resultCount = 1, maxJmp;
	private Main m;
	
	public KnightJumping(Board board, int x, int y, Main m) {
		knight = new Knight(board);
		root = board.getBoard()[x][y];
		maxJmp = board.getX() * board.getY() - 1;
		this.m = m;
	}


	public void printPath() throws InterruptedException {
		root.setVisited(true);
		searchPath(root, 0);
	}

	private String constructPath(Node node) {
		StringBuilder sb = new StringBuilder();
		while (node.getFather() != null) {
			sb.append(node).append("<-");
			node = node.getFather();
		}
		sb.append(node + "\n\n");
		return sb.toString();
	}
	
	private void searchPath(Node currentNode, int jumpFieldCount) throws InterruptedException {
		m.getPanels()[currentNode.getX()][currentNode.getY()].setBackground(Color.green);
		Thread.sleep(50);
		m.update(m.getGraphics());
		
		currentNode.setVisited(true);
		if (jumpFieldCount == maxJmp) {
			System.out.println("Lösung nummer :" + resultCount++);
			System.out.println(constructPath(currentNode));
			currentNode.setVisited(false);
			return;
		}
		ArrayList<Node> nodeList = knight.giveReachableNodes(currentNode);
		for(Node son : nodeList) {
			if (!son.isVisited()) {
				m.getPanels()[son.getX()][son.getY()].setBackground(Color.red);
				Thread.sleep(50);
				m.update(m.getGraphics());

				son.setFather(currentNode);
				searchPath(son, jumpFieldCount + 1);
			}
		}
		currentNode.setVisited(false);
		m.getPanels()[currentNode.getX()][currentNode.getY()].setBackground(Color.blue);
		Thread.sleep(50);
		m.update(m.getGraphics());

	}
	
	public static void main(String[] args) throws InterruptedException {
		KnightJumping t = new KnightJumping(new Board(5, 5), 0, 0, null);
		t.printPath();
	}
}
