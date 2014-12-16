package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;

@SuppressWarnings("serial")
public class MarketAgent extends Agent
{
	private boolean isDone = false;
	
	private static final Logger LOGGER =
			Logger.getLogger(MarketAgent.class.getName());
	
	public static final String SERVICE_DESCIPTION = "FISH_MARKET_SERVICE";
	
	@Override
	protected void setup()
	{
		// Register service to DF
        try
        {
            _register2DF();
        }
        catch (FIPAException ex)
        {
            MarketAgent.LOGGER.log(Level.SEVERE, null, ex);
        }
        
        // Add behaviors
        this.addBehaviour(new BidderManagementBehaviour(this));
        this.addBehaviour(new SellerManagementBehaviour(this));
        this.addBehaviour(new AuctionManagementBehaviour(this));
	}
	
	@Override
	protected void takeDown()
	{
		// De-register service from DF
        try
        {
            DFService.deregister(this);
        }
        catch (FIPAException fe)
        {
            MarketAgent.LOGGER.log(Level.SEVERE, null, fe);
        }
        
        super.takeDown();
	}
	
	/**
	 * Defines whether this agent reached a end state.
	 * @param isDone true to tag this agent as a ended agent (which reached a ending state in his FSMBehaviour).
	 */
	public void setIsDone(boolean isDone)
	{
		this.isDone = isDone;
	}
	
	/**
	 * Tells whether this agent reached a end state or not.
	 * @return true if this agent reached a end state in his FSMBehaviour, false otherwise.
	 */
	public boolean isDone()
	{
		return this.isDone;
	}
    
    /**
     * Add a description of this agent's services to the DF.
     * @throws FIPAException when an error occurs.
     */
    private void _register2DF()
    		throws FIPAException
    {
        ServiceDescription sd = new ServiceDescription();
        sd.setType(MarketAgent.SERVICE_DESCIPTION);
        sd.setName(this.getAID().getName());
        
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        
        dfd.addServices(sd);
        DFService.register(this, dfd);
    }
}
