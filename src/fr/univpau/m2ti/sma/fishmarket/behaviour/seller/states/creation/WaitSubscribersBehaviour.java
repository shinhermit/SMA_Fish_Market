package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitSubscribersBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					CreateAuctionSellerFSMBehaviour.MESSAGE_FILTER,
					MessageTemplate.or(
							MessageTemplate.and(
									MessageTemplate.MatchContent(
											String.valueOf(FishMarket.Commands.COMMAND_START)),
									MessageTemplate.MatchPerformative(
											ACLMessage.INFORM)),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_SUBSCRIBE)));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitSubscribersBehaviour(
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
				WaitSubscribersBehaviour.MESSAGE_FILTER);
		
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
			System.out.println("Seller: setting transition to wait subscribers !");
			
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_SUBSCRIBERS;
			
			this.myFSM.block();
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
