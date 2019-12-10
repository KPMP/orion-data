package org.kpmp.packages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StateHandlerService {

	@Value("${state.service.host}")
	private String stateServiceHost;
	@Value("${state.service.endpoint}")
	private String stateServiceEndpoint;
	private RestTemplate restTemplate;
	private LoggingService logger;

	@Autowired
	public StateHandlerService(RestTemplate restTemplate, LoggingService logger) {
		this.restTemplate = restTemplate;
		this.logger = logger;
	}

	public Map<String, State> getState() {
		Map<String, State> stateMap = new HashMap<String, State>();

		String uri = stateServiceHost + stateServiceEndpoint;
		ResponseEntity<List<State>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<State>>() {
				});
		List<State> states = response.getBody();

		for (int i = 0; i < states.size(); i++) {
			State state = states.get(i);
			stateMap.put(state.getPackageId(), state);
		}

		return stateMap;
	}

	public void sendStateChange(String packageId, String stateString, Boolean largeUploadChecked, String codicil,
								String origin) {
		State state = new State(packageId, stateString, largeUploadChecked, codicil);
		String stateId = restTemplate.postForObject(stateServiceHost + stateServiceEndpoint + "/host/" + origin,
				state, String.class);

		if (stateId == null) {
			logger.logErrorMessage(this.getClass(), null, packageId,
					this.getClass().getSimpleName() + ".sendStateChange",
					"Error saving state change for package id: " + packageId + " and state: " + stateString);
		}
	}
}
