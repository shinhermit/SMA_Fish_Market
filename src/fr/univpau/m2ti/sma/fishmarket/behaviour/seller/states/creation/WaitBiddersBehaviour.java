package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RegisterAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitBiddersBehaviour extends WakerBehaviour
{
	/** The transition which will be selected. */
	private int transition;
	
	private static final long WAIT_BIDDER_DELAY = 5000l; // 1 min
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					RegisterAuctionBehaviour.MESSAGE_FILTER,
					MessageTemplate.MatchPerformative(
							FishMarket.Performatives.TO_SUBSCRIBE));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitBiddersBehaviour(
			SellerAgent mySellerAgent)
	{
		super(mySellerAgent, WAIT_BIDDER_DELAY);
	}
	
	@Override
	public void onWake()
	{
		this.restart();
		
		// DEBUG
		System.out.println("Seller: awaken !");
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitBiddersBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.transition =
					RegisterAuctionBehaviour.TRANSITION_TO_TERMINATE_SUCCESS;
		}
		else
		{
			this.transition =
					RegisterAuctionBehaviour.TRANSITION_TO_TERMINATE_CANCEL;
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
