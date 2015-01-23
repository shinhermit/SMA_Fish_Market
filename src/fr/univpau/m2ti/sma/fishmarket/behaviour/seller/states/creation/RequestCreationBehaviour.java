package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionCreationManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class RequestCreationBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** The maximal number of attempts to register an auction. */
	private static final int MAX_REQUEST_ATTEMPTS = 4;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public RequestCreationBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		if(this.myFSM.getRequestCount() < MAX_REQUEST_ATTEMPTS)
		{
			// DEBUG
			System.out.println("Seller: requesting auction creation !");
			
			SellerAgent mySellerAgent =
					(SellerAgent) super.myAgent;
			
			ACLMessage mess = new ACLMessage(
					FishMarket.Performatives.TO_CREATE);
			
			// Receiver
			mess.addReceiver(mySellerAgent.getMarketAgent());
			
			// Set topic
			mess.addReceiver(
					AuctionCreationManagementFSMBehaviour.MESSAGE_TOPIC);
			
			// Add starting price
			mess.setContent(String.valueOf(
					mySellerAgent.getCurrentPrice()));
			
			// Send
			mySellerAgent.send(mess);
			
			this.myFSM.notifyNewRequest();
			
			// Select next transition
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_RESPONSE;
			
			// DEBUG
			System.out.println("Seller: transition set to wait response !");
		}
		else
		{
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_TERMINATE_FAILURE;
			
			// DEBUG
			System.out.println("Seller: transition set to terminate failure !");
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
