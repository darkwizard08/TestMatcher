package no.kitttn.testmatcher.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.test.mock.MockApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.kitttn.testmatcher.App;
import no.kitttn.testmatcher.R;
import no.kitttn.testmatcher.activities.MatcherActivity;
import no.kitttn.testmatcher.presenters.GeneratorPresenter;
import no.kitttn.testmatcher.utils.Util;
import no.kitttn.testmatcher.views.GeneratorView;

/**
 * @author kitttn
 */
public class GenerateFragment extends Fragment implements GeneratorView {
	@Bind(R.id.fragGenerateBtn)
	Button genBtn;
	@Inject GeneratorPresenter presenter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_generate, container, false);
		ButterKnife.bind(this, view);
		initPresenter();

		genBtn.setOnClickListener(this::loadPersonList);

		return view;
	}

	private void initPresenter() {
		((App) getActivity().getApplication()).getComponent().inject(this);
		presenter.setView(this);
	}

	private void loadPersonList(View v) {
		presenter.generateList();
	}

	@Override
	public void loading() {
		Util.showLoading(getActivity());
	}

	@Override
	public void listLoaded() {
		Util.hideLoading();
		Intent i = new Intent(getActivity(), MatcherActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		getActivity().finish();
	}
}
