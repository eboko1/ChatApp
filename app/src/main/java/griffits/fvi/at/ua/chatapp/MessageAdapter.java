package griffits.fvi.at.ua.chatapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vika on 24.07.2017.
 */

public class MessageAdapter extends ArrayAdapter<Message> {


    public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);

        }
        TextView itemTextView = (TextView)convertView.findViewById(R.id.itemTextView);
        TextView usernameTextView = (TextView)convertView.findViewById(R.id.usernameTextView);

        Message message = getItem(position);

        itemTextView.setText(message.getText());
        usernameTextView.setText(message.getUserName());


        return convertView;
    }

}
