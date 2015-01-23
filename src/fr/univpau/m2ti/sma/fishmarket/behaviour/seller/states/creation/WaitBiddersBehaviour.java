package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitBiddersBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;

	/** The duration of one cycle of wait for bidders. */
	private static final long WAIT_BIDDER_DELAY = 5000l; // 5 sec
	
	/** The max number of <i>sleep</i> cycle allowed to wait for bidders subscriptions. */
	private static final int MAX_CYCLE_COUNT = 4;
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					CreateAuctionBehaviour.MESSAGE_FILTER,
					MessageTemplate.MatchPerformative(
							FishMarket.Performatives.TO_SUBSCRIBE));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitBiddersBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionBehaviour myFSM)
	{
		super(mySellerAgent, WAIT_BIDDER_DELAY);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void onWake()
	{
		this.myFSM.notifyNewWaitCycle();
		
		// DEBUG
		System.out.println("Seller: waiting bidder sleep delay over !");
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitBiddersBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.transition =
					CreateAuctionBehaviour.TRANSITION_TO_TERMINATE_SUCCESS;
			
			// DEBUG
			System.out.println("Seller: setting transition to terminate success !");
		}
		else
		{
			if(this.myFSM.getWaitCycleCount() > WaitBiddersBehaviour.MAX_CYCLE_COUNT)
			{
				this.transition =
						CreateAuctionBehaviour.TRANSITION_TO_TERMINATE_CANCEL;
				
				// DEBUG
				System.out.println("Seller: setting transition to terminate cancel !");
			}
			else
			{
				this.transition =
						CreateAuctionBehaviour.TRANSITION_TO_WAIT_BIDDERS;
				
				// DEBUG
				System.out.println("Seller: setting transition to wait bidders !");
				
				this.reset(WAIT_BIDDER_DELAY);
			}
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
