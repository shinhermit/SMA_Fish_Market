package fr.univpau.m2ti.sma.fishmarket.agent;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.BidderBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription.SubscriptionProcessStartBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BidderAgent extends Agent
{
	protected Logger logger =
			Logger.getLogger(BidderAgent.class.getName());

	@Override
	protected void setup()
	{
		// Look up market
		super.setup();

		// Register behaviours
		this.addBehaviour(new SubscriptionProcessStartBehaviour(this));
	}

	@Override
	protected void takeDown()
	{
		// Cancel auction
		super.takeDown();
	}

	/**
	 * Called at the end of the auction subscription process.
	 *
	 * @param seller
	 */
	public void createBidderFSM(AID seller, long maxPrice)
	{
		// TODO: implement
		this.addBehaviour(new BidderBehaviour(this, seller, maxPrice));
	}

	public AID getMarketAgentAID()
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
				logger.log(Level.SEVERE, null, "Logic error: multiple market agents found !");
			}
		}
		catch (FIPAException fe)
		{
			logger.log(Level.SEVERE, null, fe);
		}

		return marketAgent;
	}
}
