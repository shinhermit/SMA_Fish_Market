package fr.univpau.m2ti.sma.fishmarket.data;

import jade.core.AID;
import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Auction implements Serializable
{
	private AID sellerID;
	
	public Auction(AID sellerID)
	{
		this.sellerID = sellerID;
	}
	
	public Auction()
	{
		this(null);
	}

	public AID getSellerID()
	{
		return sellerID;
	}

	public void setSellerID(AID sellerID)
	{
		this.sellerID = sellerID;
	}
}
