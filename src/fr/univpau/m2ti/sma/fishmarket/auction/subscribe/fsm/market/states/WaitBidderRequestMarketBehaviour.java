package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class WaitBidderRequestMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SubscribeToAuctionMarketFSMBehaviour myFSM;
	
	/** Will hold the selected transition among those to the next possible states. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					SubscribeToAuctionMarketFSMBehaviour.MESSAGE_FILTER,
					MessageTemplate.or(
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_REQUEST),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_SUBSCRIBE)));
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public WaitBidderRequestMarketBehaviour(
			MarketAgent myMarketAgent,
			SubscribeToAuctionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitBidderRequestMarketBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			this.restart();
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_REQUEST)
			{
				this.transition =
						SubscribeToAuctionMarketFSMBehaviour
						.TRANSITION_TO_PROVIDE_AUCTION_LIST;
			}
			else
			{
				this.transition =
						SubscribeToAuctionMarketFSMBehaviour
						.TRANSITION_TO_VALUATE_REQUEST;
			}
		}
		else
		{
			this.transition =
					SubscribeToAuctionMarketFSMBehaviour
					.TRANSITION_TO_WAIT_REQUEST;
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
