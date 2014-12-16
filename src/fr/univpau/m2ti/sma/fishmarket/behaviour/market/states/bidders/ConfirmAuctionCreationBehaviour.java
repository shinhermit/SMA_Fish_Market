package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behavior which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ConfirmAuctionCreationBehaviour extends OneShotBehaviour
{
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(MarketAgent.class.getName());
	
	/**
	 * Creates a behavior which is to be associated with a state of the 
	 * market agent's FSM behavior.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behavior is to be associated.
	 */
	public ConfirmAuctionCreationBehaviour(MarketAgent myMarketAgent)
	{
	}
	
	@Override
	public void action()
	{
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		try
		{
			msg.setContentObject(myMarketAgent.getAuctionCreationRequest());
			
			// De-register auction request
			myMarketAgent.registerAuctionCreationRequest(null);
			
			myAgent.send(msg);
		}
		catch (IOException e)
		{
			ConfirmAuctionCreationBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
	}
}