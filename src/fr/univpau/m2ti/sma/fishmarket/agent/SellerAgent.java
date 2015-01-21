package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionBehaviour;

@SuppressWarnings("serial")
/**
 * A seller agent of the fish market protocol.
 * 
 * @author Josuah Aron
 *
 */
public class SellerAgent extends AbstractMarketUser
{
	/** The AID of the market agent. */
	private AID marketAgentID = null;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(SellerAgent.class.getName());

	/**
	 * Creates a seller agent of the fish market protocol.
	 */
	public SellerAgent()
	{}
	
	@Override
	protected void setup()
	{
		this.addBehaviour(
				new CreateAuctionBehaviour(this));
		// AuctionSellerBehaviour is to be added when the first one terminates.
		
		this.marketAgentID =
				lookupMarketAgent();
	}
    
	/**
	 * Retrieves the AID of the market agent.
	 * @return the AID of the market agent.
	 */
    protected AID lookupMarketAgent()
    {
    	AID marketAgent = null;
    	
        DFAgentDescription marketTemplate = new DFAgentDescription();
        ServiceDescription marketSd = new ServiceDescription();
        
        // Template for searching the market agent
        marketSd.setType(MarketAgent.SERVICE_DESCIPTION);
        marketTemplate.addServices(marketSd);
        
        try
        {
            DFAgentDescription[] marketResult = DFService.search(this, marketTemplate);
            
            if(marketResult.length == 1)
            {
            	marketAgent = marketResult[0].getName();
            }
            else
            {
            	SellerAgent.LOGGER.log(
            			Level.SEVERE, null,
            			"Logic error: multiple market agents found !");
            }
        }
        catch (FIPAException fe)
        {
            SellerAgent.LOGGER.log(Level.SEVERE, null, fe);
        }
        
        return marketAgent;
    }

    /**
     * 
     * @return the AID of the market agent.
     */
	public AID getMarketAgent()
	{
		return marketAgentID;
	}
}
