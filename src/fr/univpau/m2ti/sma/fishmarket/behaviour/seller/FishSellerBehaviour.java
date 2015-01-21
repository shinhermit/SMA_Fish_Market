package fr.univpau.m2ti.sma.fishmarket.behaviour.seller;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import jade.core.behaviours.FSMBehaviour;

@SuppressWarnings("serial")
/**
 * The behaviour of the seller agent when attending his auction.
 * 
 * @author Josuah Aron
 *
 */
public class FishSellerBehaviour extends FSMBehaviour
{
	/** The id of the conversation of this auction. */
	private String conversationId;
	
	/**
	 * Creates the behaviour of the seller agent when his auction is running.
	 * 
	 * @param mySellerAgent the seller agent to which the behaviour is to be added.
	 */
	public FishSellerBehaviour(
			SellerAgent mySellerAgent, String conversationId)
	{
		super(mySellerAgent);
		
		this.conversationId = conversationId;
		
		// Add states
		// TODO : final state must create AuctionSellerBehaviour
		
		// Add transitions
	}
}
