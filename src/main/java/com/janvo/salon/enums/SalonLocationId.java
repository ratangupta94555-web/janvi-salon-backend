package com.janvo.salon.enums;

public enum SalonLocationId {
	CHITAIPUR(
			"Janvi Makeover & Academy · Chitaipur",
			"Central Bank Building, opposite Gyandeep Academy, Vishwakarma Nagar, Mohanpuri Colony, Chitaipur, Varanasi"),
	RIHAND_NAGAR(
			"Anu beauty salon & Jaanvi makeover Academy · Rihand Nagar",
			"Mansarovar Shopping Complex, NTPC Campus, Bijpur, Rihand Nagar, Uttar Pradesh 231223");

	private final String name;
	private final String address;

	SalonLocationId(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
}
