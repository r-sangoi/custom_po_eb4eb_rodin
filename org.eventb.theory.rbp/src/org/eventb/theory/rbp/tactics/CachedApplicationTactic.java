/*******************************************************************************
 * Copyright (c) 2020, 2021 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CentraleSupélec - initial implementation
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Tactic provider implementation caching the results of another tactic
 * provider.
 *
 * The tactic applications are cached based on four parameters:
 * <ul>
 * <li>the current proof attempt;</li>
 * <li>a predicate that is either an hypothesis or the goal depending on the
 * value of the next parameter;</li>
 * <li>a boolean indicating whether the predicate is an hypothesis or the
 * goal;</li>
 * <li>the {@code globalInput} parameter of
 * {@link ITacticProvider#getPossibleApplications(IProofTreeNode, Predicate, String)}
 * </ul>
 *
 * Do not use this class with a tactic provider which results depend on anything
 * other than these four parameters.
 *
 * This registers itself as a change listener of the user support manager. Then,
 * when a proof attempt is removed, the associated cached results are also
 * removed.
 *
 * @author Guillaume Verdier
 */
public class CachedApplicationTactic implements ITacticProvider, IUserSupportManagerChangedListener {

	/**
	 * Group of three cache parameters: the predicate, the boolean indicating
	 * whether this predicate is an hypothesis or a goal, and the global input.
	 *
	 * The fourth parameter, the proof attempt, is handled separately in order to be
	 * able to quickly remove all cached results associated to a given proof
	 * attempt.
	 *
	 * Once they have been initialized, objects of this class are immutable.
	 */
	protected static class TacticCacheKey {

		/**
		 * The predicate which is either the {@code hyp} parameter of
		 * {@link ITacticProvider#getPossibleApplications(IProofTreeNode, Predicate, String)}
		 * or the goal of the current proof, if {@code hyp} is null.
		 */
		private final Predicate predicate;

		/**
		 * {@code true} if {@link #predicate} is a proof goal, {@code false} if it is an
		 * hypothesis.
		 */
		private final boolean isGoal;

		/**
		 * The {@code globalInput} parameter of
		 * {@link ITacticProvider#getPossibleApplications(IProofTreeNode, Predicate, String)}.
		 */
		private final String globalInput;

		/**
		 * Builds a cache key; the object is immutable after its creation.
		 *
		 * @param predicate   a proof hypothesis or goal
		 * @param isGoal      {@code true} if {@code predicate} is a goal, {@code false}
		 *                    otherwise
		 * @param globalInput the global input
		 */
		public TacticCacheKey(Predicate predicate, boolean isGoal, String globalInput) {
			this.predicate = predicate;
			this.isGoal = isGoal;
			this.globalInput = globalInput;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + predicate.hashCode();
			result = prime * result + Boolean.hashCode(isGoal);
			result = prime * result + (globalInput == null ? 0 : globalInput.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !getClass().equals(obj.getClass())) {
				return false;
			}
			TacticCacheKey other = (TacticCacheKey) obj;
			return predicate.equals(other.predicate) && isGoal == other.isGoal
					&& (globalInput == null ? other.globalInput == null : globalInput.equals(other.globalInput));
		}

	}

	/**
	 * Container of the cache for tactic application results.
	 *
	 * All methods are thread-safe.
	 */
	protected static class TacticCache {

		/**
		 * The associative map containing the cached results.
		 *
		 * The cache keys are split in two groups: each proof attempt is associated to a
		 * map, which associates results to the predicate, the boolean {@code isGoal},
		 * and the globalInput string. This makes is easier and faster to remove all
		 * cached results associated to a specific proof attempt when it is disposed.
		 */
		private final Map<IProofAttempt, Map<TacticCacheKey, List<ITacticApplication>>> cache = new HashMap<IProofAttempt, Map<TacticCacheKey, List<ITacticApplication>>>();

		/**
		 * Retrieves a cached result.
		 *
		 * This method is thread-safe.
		 *
		 * @param attempt     the current proof attempt
		 * @param pred        the hypothesis or goal to which the applications apply
		 * @param isGoal      {@code true} if {@code pred} is a goal, {@code false}
		 *                    otherwise
		 * @param globalInput the global input
		 * @return the cached result if it exists, {@code null} otherwise
		 */
		public synchronized List<ITacticApplication> get(IProofAttempt attempt, Predicate pred, boolean isGoal,
				String globalInput) {
			Map<TacticCacheKey, List<ITacticApplication>> attemptCache = cache.get(attempt);
			if (attemptCache == null) {
				return null;
			}
			TacticCacheKey cacheKey = new TacticCacheKey(pred, isGoal, globalInput);
			return attemptCache.get(cacheKey);
		}

		/**
		 * Caches a result.
		 *
		 * This method is thread-safe.
		 *
		 * @param attempt      the current proof attempt
		 * @param pred         the hypothesis or goal to which the applications apply
		 * @param isGoal       {@code true} if {@code pred} is a goal, {@code false}
		 *                     otherwise
		 * @param globalInput  the global input
		 * @param applications the result of
		 *                     {@link ITacticProvider#getPossibleApplications(IProofTreeNode, Predicate, String)}
		 *                     to add to the cache
		 */
		public synchronized void put(IProofAttempt attempt, Predicate pred, boolean isGoal, String globalInput,
				List<ITacticApplication> applications) {
			Map<TacticCacheKey, List<ITacticApplication>> attemptCache = cache.get(attempt);
			if (attemptCache == null) {
				attemptCache = new HashMap<TacticCacheKey, List<ITacticApplication>>();
				cache.put(attempt, attemptCache);
			}
			TacticCacheKey cacheKey = new TacticCacheKey(pred, isGoal, globalInput);
			attemptCache.put(cacheKey, applications);
		}

		/**
		 * Cleans the cache by removing cached results associated to disposed proof
		 * attempts.
		 *
		 * This method is thread-safe.
		 */
		public synchronized void clean() {
			cache.keySet().removeIf(IProofAttempt::isDisposed);
		}

	}

	/**
	 * The original tactic provider which results are cached.
	 */
	protected final ITacticProvider tactic;

	/**
	 * The actual cache associating the results to the parameters.
	 */
	protected TacticCache cache = new TacticCache();

	/**
	 * Creates a cached application tactic from a given tactic provider.
	 *
	 * @param tactic the tactic provider which results will be cached
	 */
	public CachedApplicationTactic(ITacticProvider tactic) {
		this.tactic = tactic;
		EventBPlugin.getUserSupportManager().addChangeListener(this);
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt) {
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			boolean isGoal = hyp == null;
			Predicate pred = (isGoal ? node.getSequent().goal() : hyp);
			List<ITacticApplication> result = cache.get(attempt, pred, isGoal, globalInput);
			if (result != null) {
				return result;
			}
			result = tactic.getPossibleApplications(node, hyp, globalInput);
			/*
			 * The cache is cleaned in the listener for user support changes, but we also do
			 * it here so that the cache is at least cleaned sometimes, even if the listener
			 * is removed.
			 */
			cache.clean();
			cache.put(attempt, pred, isGoal, globalInput, result);
			return result;
		} else {
			/*
			 * We can't cache results if the proof origin is not a proof attempt. This case
			 * doesn't seem to happen in practice, but if it does, we just pass through to
			 * the actual tactic provider, hoping that it can deal with it.
			 */
			return tactic.getPossibleApplications(node, hyp, globalInput);
		}
	}

	@Override
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		/* Some proof attempts may have been disposed: remove them from the cache */
		cache.clean();
	}

	/**
	 * Removes this object from the user support manager's change listeners.
	 *
	 * This should be called before the object destruction.
	 *
	 * Other methods are still safe to use after calling this one, but the cached
	 * results may not be removed, causing an increased memory consumption.
	 */
	public void dispose() {
		EventBPlugin.getUserSupportManager().removeChangeListener(this);
	}

}
