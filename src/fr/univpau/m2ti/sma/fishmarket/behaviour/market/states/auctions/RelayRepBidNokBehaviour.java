package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class RelayRepBidNokBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private AuctionManagementBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public RelayRepBidNokBehaviour(
			MarketAgent myMarketAgent,
			AuctionManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		ACLMessage toRelay = this.myFSM.getRequest();
		
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
		toRelay.clearAllReceiver();
		
		// Put back message topic
		toRelay.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		for(AID subscriber : myMarketAgent.getSubscribers(
				this.myFSM.getSeller()))
		{
			toRelay.addReceiver(subscriber);
		}
		
		super.myAgent.send(toRelay);
		
		this.myFSM.setRequest(null);
		
		this.myFSM.clearBidderList();
	}
}
