package concurrentSystems;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

/**
 * 点情報を収集するエージェント
 * 
 *  */
public class PointCollector extends Agent {
	/**
	 * 初期設定<br>
	 * ビヘイビアを設定
	 *  */
	protected void setup() {
		// 実行時のAgentを全取得
		final AMSAgentDescription[] agents = getAgents(); 
		addBehaviour(new CollectBehaviour(agents));
	}
	/**
	 * 点情報を収集するふるまい<br>
	 * 実行内容<br>
	 * 他のエージェントが実行した内容を収集する<br>
	 * 終了条件はある指定回数のエージェントの結果を収集できたとき
	 * @author raedion
	 *  */
	class CollectBehaviour extends SimpleBehaviour {
		int n = 0;
		boolean isRecved = true;
		int recvNum = 0;
		long allPoints = 0;
		long inPoints = 0;
		final int INITAGENTS = 3;				// 初期に呼び出すエージェント数
		final int MYSELF = 1;					// 自分自身のエージェント数
		
		final AMSAgentDescription[] agents;
		final int lenOfAgents;
		
		public CollectBehaviour(AMSAgentDescription[] agents) {
			this.agents = agents;
			this.lenOfAgents = agents.length - INITAGENTS - MYSELF;
		}
		
		/**
		 * ビヘイビアの具体的な挙動<br>
		 * データを送信したあとはその返答が全て来るまで待機<br>
		 * データを全てのエージェントに送信
		 * @author raedion
		 *  */
		@Override
		public void action() {
			if (isRecved) {
				n++;
				for (int i = 0; i < agents.length; i++) {
					if (agents[i].getName().equals(getAID())) continue;
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent("Send");
					msg.addReceiver(agents[i].getName());
					send(msg);
				}
				isRecved = false;
				return;
			}
			else {
				ACLMessage msg = receive();
				if (msg != null) {
					recvNum++;
					if (recvNum >= lenOfAgents) {
						recvNum = 0;
						isRecved = true;
						return;
					}
					allPoints++;
					inPoints = inPoints + (msg.getContent().equals("1") ? 1 : 0);
				}
				block(5);
			}
		}
		
		/**
		 * 実行終了条件を指定
		 */
		@Override
		public boolean done() {
			return n >= 100;	// FOR DEBUG
		}
		
		@Override
		public int onEnd() {
			System.out.println("Result is : " + (double) inPoints / allPoints * 4);
			return 0;
		}
	}
	private AMSAgentDescription[] getAgents() {
		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println("Problem searching AMS: " + e);
			e.printStackTrace();
		}
		return agents;
	}
}
