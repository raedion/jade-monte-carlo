package concurrentSystems;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PointCreator extends Agent {
	protected void setup(){
		addBehaviour(new ThrowingBehaviour());
	}
	class ThrowingBehaviour extends CyclicBehaviour {
		/** 
		 * ビヘイビアの実行内容<br>
		 * 送信を受理次第計算を実行してその結果を相手に送信する
		 * */
		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {				// 実行指令がきた時
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				double x = Math.random();
				double y = Math.random();
				String str = isInnerCircle(x, y) ? "1" : "0";
				reply.setContent(str);
				send(reply);
			}
			block();
		}
		/** 
		 * 円の内部に点が存在するか
		 * @author raedion
		 * @param x x軸方向の値
		 * @param y y軸方向の値
		 * */
		private boolean isInnerCircle(double x, double y) {
			return Math.pow(x - 0.5, 2) + Math.pow(y - 0.5, 2) < 0.25;
		}
	}
}
