package fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitResponseSellerBehaviour extends OneShotBehaviour
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
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_ACCEPT),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_REFUSE)));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitResponseSellerBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// DEBUG
		System.out.println("Seller: checking messages !");
		
		SellerAgent mySellerAgent = (SellerAgent) super.myAgent;
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitResponseSellerBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.myFSM.setResponse(mess);
			
			// Reset blocking state
			this.restart();
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_ACCEPT)
			{
				this.transition =
						CreateAuctionSellerFSMBehaviour
						.TRANSITION_TO_WAIT_SUBSCRIBERS;
				
				mySellerAgent.notifyAuctionCreated();
				
				// DEBUG
				System.out.println("Seller: setting transition to wait subscribers !");
			}
			else
			{
				this.transition =
						CreateAuctionSellerFSMBehaviour
						.TRANSITION_TO_REQUEST_CREATION;
				
				// DEBUG
				System.out.println("Seller: setting transition to request creation !");
			}
		}
		else
		{
			this.transition =
					CreateAuctionSellerFSMBehaviour
					.TRANSITION_TO_WAIT_RESPONSE;
			
			// DEBUG
			System.out.println("Seller: transition to wait response !");
			
			// DEBUG
			System.out.println("Seller: blocking FSM for wainting messages !");
			
			// Wait that myAgent receives message
			this.myFSM.block();
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
