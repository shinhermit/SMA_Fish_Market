package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class RelayToAttributeBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public RelayToAttributeBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionManagementFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		// DEBUG
		System.out.println("Market: relaying to attribute !");
		
		ACLMessage request = this.myFSM.getRequest();
		
		// RELAY
		ACLMessage toRelay = new ACLMessage(
				request.getPerformative());
		
		toRelay.addReceiver(
				this.myFSM.getSelectedBidder());
		
		// Message topic
		toRelay.addReceiver(
				RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC);
		
		// Conversation ID
		toRelay.setConversationId(
				request.getConversationId());
		
		super.myAgent.send(toRelay);
		
		// Delete request
		this.myFSM.setRequest(null);
	}
}
