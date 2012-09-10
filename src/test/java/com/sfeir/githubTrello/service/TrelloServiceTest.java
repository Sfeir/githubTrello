package com.sfeir.githubTrello.service;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.sfeir.githubTrello.Tools.*;
import static org.fest.assertions.Assertions.*;


public class TrelloServiceTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		service = new ExpandedTrelloService(TRELLO_TOKEN);

		List toDoList = service.getList(BOARD, FIRST_LIST.getName());
		List doingList = service.getList(BOARD, SECOND_LIST.getName());

		assertThat(toDoList.getId()).isEqualTo(FIRST_LIST.getId());
		assertThat(doingList.getId()).isEqualTo(SECOND_LIST.getId());
		assertThat(toDoList.getCards()).containsOnly(CARD_1, CARD_2, CARD_3);
		assertThat(doingList.getCards()).isEmpty();
	}

	@Test
	public void should_have_one_card_in_second_list() {
		service.moveCard(CARD_1, SECOND_LIST);
		assertThat(service.getList(BOARD, FIRST_LIST.getName()).getCards()).containsOnly(CARD_2, CARD_3);
		assertThat(service.getList(BOARD, SECOND_LIST.getName()).getCards()).containsOnly(CARD_1.inNewList(SECOND_LIST));
	}

	@Test
	public void should_have_correct_card_info() {
		assertThat(service.getCard(CARD_1.getId())).isEqualTo(CARD_1);
		assertThat(service.getCard(CARD_2.getId())).isEqualTo(CARD_2);
		assertThat(service.getCard(CARD_3.getId())).isEqualTo(CARD_3);
	}

	@After
	public void tearDown() throws Exception {
		service.moveCard(CARD_1, FIRST_LIST);
		service.moveCard(CARD_2, FIRST_LIST);
		service.moveCard(CARD_3, FIRST_LIST);
	}

	private static ExpandedTrelloService service;

	private static final Board BOARD = new Board("504df45b6d2da1e52e77a5fa");
	private static final String TRELLO_TOKEN = "7e8c025e357e5d3d65920111613adabc6fd21072600951a7234030a3280a3d21";
	private static final List FIRST_LIST = list("504df45b6d2da1e52e77a5fb", "To Do");
	private static final List SECOND_LIST = list("504df45b6d2da1e52e77a5fc", "Doing");
	private static final Card CARD_1 = card("Card 1", "", "504df4656d2da1e52e77a9bc", FIRST_LIST.getId(), BOARD.getId());
	private static final Card CARD_2 = card("Card 2", "", "504df4696d2da1e52e77ae2d", FIRST_LIST.getId(), BOARD.getId());
	private static final Card CARD_3 = card("Card 3", "", "504df4706d2da1e52e77b234", FIRST_LIST.getId(), BOARD.getId());


	private static class ExpandedTrelloService extends TrelloService {
		ExpandedTrelloService(String token) {
			super(token);
		}

		void moveCard(Card card, List list) {
			restClient.url("/cards/%s/idList?value=%s", card.getId(), list.getId()).put();
		}
	}
}
