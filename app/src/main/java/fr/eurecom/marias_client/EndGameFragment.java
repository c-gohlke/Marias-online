package fr.eurecom.marias_client;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EndGameFragment extends DialogFragment {
    public EndGameFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EndGameFragment newInstance(String winner) {
        EndGameFragment frag = new EndGameFragment();
        Bundle args = new Bundle();
        args.putString("winner", winner);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.end_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String winner = getArguments().getString("winner", "");
        TextView winner_msg = (TextView) view.findViewById(R.id.winner_msg);
        winner_msg.setText("Winner is: " + winner);
    }
}
