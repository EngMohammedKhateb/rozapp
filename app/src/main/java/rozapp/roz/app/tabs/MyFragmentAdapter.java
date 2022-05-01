package rozapp.roz.app.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyFragmentAdapter  extends FragmentStateAdapter {

    public  MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0: return new FragmentAll();
            case 1: return new FragmentOnline();
            case 2: return new FragmentFollowers();
            case 3: return new FragmentFollowing();
            default : return new FragmentAll();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}