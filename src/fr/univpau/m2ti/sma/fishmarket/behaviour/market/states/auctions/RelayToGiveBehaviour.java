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
public class RelayToGiveBehaviour extends OneShotBehaviour
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
	public RelayToGiveBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionManagementFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		ACLMessage toRelay = this.myFSM.getRequest();
		
		toRelay.clearAllReceiver();
		
		toRelay.addReceiver(
				this.myFSM.getSelectedBidder());
		
		// Put back message topic
		toRelay.addReceiver(
				RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC);
		
		super.myAgent.send(toRelay);
		
		// Delete request
		this.myFSM.setRequest(null);
	}
}
