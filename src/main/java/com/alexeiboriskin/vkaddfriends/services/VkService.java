package com.alexeiboriskin.vkaddfriends.services;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.friends.responses.AddResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VkService {
    private Integer userId;
    private String token;

    private List<Integer> listOfNicks = new ArrayList<>();
    private int lastAdded = -1;

    private VkApiClient vk = new VkApiClient(new HttpTransportClient());
    private UserActor actor;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void appendToList(List<Integer> listOfNicks) {
        this.listOfNicks.addAll(listOfNicks);
    }

    @Scheduled(cron="00 02 * * * *")
    private void addDailyPortionOfFriends() {
        log.info("Scheduled task started from list position: %d", lastAdded + 1);

        int i;
        for(i = lastAdded + 1;
            i < ((listOfNicks.size() - lastAdded) > 40 ? lastAdded + 40 : listOfNicks.size()); i++) {
            addFriend(listOfNicks.get(i));
        }
        lastAdded = i;

        log.info("Scheduled ended on list position: %d", lastAdded);
    }

    private void addFriend(int friendId) {
        AddResponse response;
        try {
            response = vk
                .friends()
                .add(getActor(), friendId)
                .text("Hi! Please add me as your best fried ASAP")
                .execute();

            log.info("Was trying to add friend with ID: %d and got response code: %d", friendId, response.getValue());
        } catch (ApiException e) {
            log.error("Vk API exception");
        } catch (ClientException e) {
            log.error("Vk client exception");
        }
    }

    private UserActor getActor() {
        if (actor == null) {
            actor = new UserActor(userId, token);
        }
        return actor;
    }
}
