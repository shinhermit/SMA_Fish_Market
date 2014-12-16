package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MarketAgent extends Agent
{
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
            System.err.println("TravelAgency::setup : DF registration error. "+ex);
            MarketAgent.LOGGER.log(Level.SEVERE, null, ex);
        }
        
        // Add behaviors
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
            System.err.println("TravelAgency::takeDown : DF de-registration error. "+fe);
            MarketAgent.LOGGER.log(Level.SEVERE, null, fe);
        }
        
        super.takeDown();
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
