package com.innovation.me2there;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        String message = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(StartActivity.FB_ID);


        /*
        // Create a Card
		Card card = new Card(this, R.layout.row_card);

		// Create a CardHeader
		CardHeader header = new CardHeader(this);
		header.setTitle("Hello world");
		
		card.setTitle("Simple card demo");
		CardThumbnail thumb = new CardThumbnail(this);
		thumb.setDrawableResource(R.drawable.ic_launcher);
		
		card.addCardThumbnail(thumb);
		
		// Add Header to card
		card.addCardHeader(header);

		// Set card in the cardView
		CardView cardView = (CardView) findViewById(R.id.carddemo);
		cardView.setCard(card);

        ///////////////////////////////////

*/

        //Need to retrieve the activity list and image name from DB

        int listImages[] = new int[]{R.drawable.meetup_1, R.drawable.meetup_2,
                R.drawable.meetup_3, R.drawable.meetup_4, R.drawable.meetup_5};

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i<5; i++) {
            // Create a Card
            Card cardItem = new Card(this,R.layout.row_card);
            // Create a CardHeader
            CardHeader headerItem = new CardHeader(this);
            // Add Header to card
            headerItem.setTitle("Activity  " + i);
            cardItem.setTitle("Come join us and lets have fun "+ i);


             cardItem.addCardHeader(headerItem);

            CardThumbnail thumbItem = new CardThumbnail(this);

            thumbItem.setDrawableResource(listImages[i]);
            cardItem.addCardThumbnail(thumbItem);

            cards.add(cardItem);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this, cards);

        CardListView listView = (CardListView) this.findViewById(R.id.activityList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
	}

}
