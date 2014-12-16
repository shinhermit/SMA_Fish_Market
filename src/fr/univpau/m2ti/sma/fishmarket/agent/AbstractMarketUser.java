package fr.univpau.m2ti.sma.fishmarket.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class AbstractMarketUser extends Agent {
	protected AID marketAgentID;
	
	private final Logger logger =
			Logger.getLogger(MarketAgent.class.getName());
	
	@Override
	protected void setup()
	{
		// Look up market
		this.marketAgentID = this.lookupMarketAgent();
	}
	
	@Override
	protected void takeDown()
	{
		// Un-suscribe auctions
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
            	getLogger().log(Level.SEVERE, null, "Logic error: multiple market agents found !");
            }
        }
        catch (FIPAException fe)
        {
            getLogger().log(Level.SEVERE, null, fe);
        }
        
        return marketAgent;
    }

	final protected Logger getLogger() {
		return logger;
	}
}
