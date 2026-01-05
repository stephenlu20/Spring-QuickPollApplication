package io.zipcoder.tc_spring_poll_application.controller;

import io.zipcoder.tc_spring_poll_application.dtos.OptionCount;
import io.zipcoder.tc_spring_poll_application.dtos.VoteResult;
import io.zipcoder.tc_spring_poll_application.domain.Vote;
import io.zipcoder.tc_spring_poll_application.repositories.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ComputeResultController {

    private final VoteRepository voteRepository;

    public ComputeResultController(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @RequestMapping(value = "/computeresult", method = RequestMethod.GET)
    public ResponseEntity<?> computeResult(@RequestParam Long pollId) {
        Iterable<Vote> allVotes = voteRepository.findVotesByPoll(pollId);

        // Map to count votes per option
        Map<Long, OptionCount> countMap = new HashMap<>();
        int totalVotes = 0;

        for (Vote vote : allVotes) {
            totalVotes++;
            Long optionId = vote.getOption().getId();

            // If the option is not yet in the map, create a new OptionCount
            countMap.putIfAbsent(optionId, new OptionCount());
            OptionCount oc = countMap.get(optionId);
            oc.setOptionId(optionId);
            oc.setCount(oc.getCount() + 1);
        }

        // Convert map values to a list
        List<OptionCount> results = new ArrayList<>(countMap.values());

        // Create VoteResult object
        VoteResult voteResult = new VoteResult();
        voteResult.setTotalVotes(totalVotes);
        voteResult.setResults(results);

        return new ResponseEntity<>(voteResult, HttpStatus.OK);
    }
}
