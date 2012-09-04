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
		service = new ExpandedTrelloService("0b2755de4a62ec8b473b6040e38ed6aaa60f8c6c358c3598bfd7721038247f45");

		List toDoList = service.getList(board, FIRST_LIST.getName());
		List doingList = service.getList(board, SECOND_LIST.getName());

		assertThat(toDoList.getId()).isEqualTo(FIRST_LIST.getId());
		assertThat(doingList.getId()).isEqualTo(SECOND_LIST.getId());
		assertThat(toDoList.getCards()).containsOnly(CARD_1, CARD_2, CARD_3);
		assertThat(doingList.getCards()).isEmpty();
	}

	@Test
	public void should_have_one_card_in_second_list() {
		service.moveCard(CARD_1, SECOND_LIST);
		assertThat(service.getList(board, FIRST_LIST.getName()).getCards()).containsOnly(CARD_2, CARD_3);
		assertThat(service.getList(board, SECOND_LIST.getName()).getCards()).containsOnly(CARD_1.withNewList(SECOND_LIST));
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
	private static Board board = new Board("50364695312149d41b85ec68");
	private static final List FIRST_LIST = list("50364695312149d41b85ec69", "To Do");
	private static final List SECOND_LIST = list("50364695312149d41b85ec6a", "Doing");
	private static final Card CARD_1 = card("Card 1", "50447bbee86d9cdc5dfe50a7", FIRST_LIST.getId(), board.getId());
	private static final Card CARD_2 = card("Card 2", "50447bc7e86d9cdc5dfe55cb", FIRST_LIST.getId(), board.getId());
	private static final Card CARD_3 = card("Card 3", "50447bd1e86d9cdc5dfe5c12", FIRST_LIST.getId(), board.getId());


	private static class ExpandedTrelloService extends TrelloService {
		public ExpandedTrelloService(String token) {
			super(token);
		}

		void moveCard(Card card, List list) {
			rest.url("/cards/%s/idList?value=%s", card.getId(), list.getId()).put();
		}
	}
}
