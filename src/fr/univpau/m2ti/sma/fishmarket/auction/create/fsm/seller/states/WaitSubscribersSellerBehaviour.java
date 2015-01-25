package fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitSubscribersSellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** the duration of one blocking cycle in order to wait for suscribers. */
	private static final long WAITING_SUBSCRIBER_CYCLE_DURATION = 500; // 0.5 sec
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					CreateAuctionSellerFSMBehaviour.MESSAGE_FILTER,
					MessageTemplate.MatchPerformative(
							FishMarket.Performatives.TO_SUBSCRIBE));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitSubscribersSellerBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		SellerAgent mySellerAgent = (SellerAgent) super.myAgent;
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitSubscribersSellerBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			if(mess.getPerformative() == FishMarket.Performatives.TO_SUBSCRIBE)
			{
				mySellerAgent.notifyNewSubscriber();
			}
			
			// DEBUG
			System.out.println("Seller: notifying new subscriber !");
		}
		
		if(mySellerAgent.isStartCommandReceived())
		{

			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_TERMINATE_SUCCESS;
			
			// DEBUG
			System.out.println("Seller: setting transition to terminate success !");
		}
		else if(mySellerAgent.isCancelCommandReceived())
		{
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_TERMINATE_CANCEL;
			
			// DEBUG
			System.out.println("Seller: setting transition to terminate cancel !");
		}
		else
		{
			// DEBUG
//			System.out.println("Seller: setting transition to wait subscribers !");
			
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_SUBSCRIBERS;
			
			this.myFSM.block(WAITING_SUBSCRIBER_CYCLE_DURATION);
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
