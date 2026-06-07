package io.yar.yar2026.friend;

import io.yar.yar2026.friend.domain.FriendRequest;
import io.yar.yar2026.friend.domain.FriendRequestStatus;
import io.yar.yar2026.friend.dto.FriendRequestCreateRequest;
import io.yar.yar2026.friend.dto.FriendRequestResponse;
import io.yar.yar2026.friend.exception.DuplicateFriendRequestException;
import io.yar.yar2026.friend.exception.SelfFriendRequestException;
import io.yar.yar2026.friend.repository.FriendRequestRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.exception.UserNotFoundException;
import io.yar.yar2026.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("FriendService")
class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("친구 요청 생성")
    class CreateFriendRequest {

        @Test
        @DisplayName("성공 시 친구 요청을 PENDING 상태로 저장하고 응답을 반환한다")
        void createFriendRequest_success() {
            // given
            Long userId = 1L;
            Long toUserId = 2L;
            User fromUser = userOf(userId, "보낸유저");
            User toUser = userOf(toUserId, "받는유저");
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(toUserId);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(fromUser));
            given(userRepository.findById(toUserId))
                    .willReturn(Optional.of(toUser));
            given(friendRequestRepository.existsBetweenUsers(userId, toUserId))
                    .willReturn(false);
            given(friendRequestRepository.save(any(FriendRequest.class)))
                    .willAnswer(invocation -> {
                        FriendRequest friendRequest = invocation.getArgument(0);
                        ReflectionTestUtils.setField(friendRequest, "friendRequestId", 10L);
                        return friendRequest;
                    });

            // when
            FriendRequestResponse response = friendService.createFriendRequest(userId, request);

            // then
            assertThat(response.friendRequestId()).isEqualTo(10L);
            assertThat(response.fromUserId()).isEqualTo(userId);
            assertThat(response.toUserId()).isEqualTo(toUserId);
            assertThat(response.status()).isEqualTo(FriendRequestStatus.PENDING);
            assertThat(response.nickname()).isEqualTo("받는유저");

            ArgumentCaptor<FriendRequest> captor = ArgumentCaptor.forClass(FriendRequest.class);
            then(friendRequestRepository).should().save(captor.capture());

            FriendRequest savedFriendRequest = captor.getValue();
            assertThat(savedFriendRequest.getFromUser()).isSameAs(fromUser);
            assertThat(savedFriendRequest.getToUser()).isSameAs(toUser);
            assertThat(savedFriendRequest.getStatus()).isEqualTo(FriendRequestStatus.PENDING);
        }

        @Test
        @DisplayName("자기 자신에게 요청하면 SelfFriendRequestException이 발생한다")
        void createFriendRequest_fail_when_self_request() {
            // given
            Long userId = 1L;
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(userId);

            // when & then
            assertThatThrownBy(() -> friendService.createFriendRequest(userId, request))
                    .isInstanceOf(SelfFriendRequestException.class)
                    .hasMessage("자기 자신에게 친구 요청을 보낼 수 없습니다.");

            then(userRepository).shouldHaveNoInteractions();
            then(friendRequestRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("요청 대상 유저가 없으면 UserNotFoundException이 발생한다")
        void createFriendRequest_fail_when_target_user_not_found() {
            // given
            Long userId = 1L;
            Long toUserId = 999L;
            User fromUser = userOf(userId, "보낸유저");
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(toUserId);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(fromUser));
            given(userRepository.findById(toUserId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> friendService.createFriendRequest(userId, request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 찾을 수 없습니다.");

            then(friendRequestRepository).should(never()).save(any(FriendRequest.class));
        }

        @Test
        @DisplayName("이미 친구 요청 또는 관계가 있으면 DuplicateFriendRequestException이 발생한다")
        void createFriendRequest_fail_when_request_exists() {
            // given
            Long userId = 1L;
            Long toUserId = 2L;
            User fromUser = userOf(userId, "보낸유저");
            User toUser = userOf(toUserId, "받는유저");
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(toUserId);

            given(userRepository.findById(userId))
                    .willReturn(Optional.of(fromUser));
            given(userRepository.findById(toUserId))
                    .willReturn(Optional.of(toUser));
            given(friendRequestRepository.existsBetweenUsers(userId, toUserId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> friendService.createFriendRequest(userId, request))
                    .isInstanceOf(DuplicateFriendRequestException.class)
                    .hasMessage("이미 친구 관계입니다. / 이미 보낸 친구 요청이 있습니다.");

            then(friendRequestRepository).should(never()).save(any(FriendRequest.class));
        }
    }

    @Nested
    @DisplayName("친구 요청 조회")
    class GetFriendRequests {

        @Test
        @DisplayName("받은 친구 요청 목록을 PENDING 상태 기준으로 반환한다")
        void getReceivedFriendRequests_success() {
            // given
            Long userId = 1L;
            User fromUser = userOf(2L, "요청보낸유저");
            User toUser = userOf(userId, "받는유저");
            FriendRequest friendRequest = friendRequestOf(10L, fromUser, toUser);

            given(friendRequestRepository.findAllByToUser_UserIdAndStatusOrderByCreatedAtDesc(
                    userId,
                    FriendRequestStatus.PENDING
            )).willReturn(List.of(friendRequest));

            // when
            List<FriendRequestResponse> response = friendService.getReceivedFriendRequests(userId);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).friendRequestId()).isEqualTo(10L);
            assertThat(response.get(0).fromUserId()).isEqualTo(2L);
            assertThat(response.get(0).toUserId()).isEqualTo(userId);
            assertThat(response.get(0).status()).isEqualTo(FriendRequestStatus.PENDING);
            assertThat(response.get(0).nickname()).isEqualTo("요청보낸유저");

            then(friendRequestRepository).should()
                    .findAllByToUser_UserIdAndStatusOrderByCreatedAtDesc(
                            userId,
                            FriendRequestStatus.PENDING
                    );
        }

        @Test
        @DisplayName("보낸 친구 요청 목록을 PENDING 상태 기준으로 반환한다")
        void getSentFriendRequests_success() {
            // given
            Long userId = 1L;
            User fromUser = userOf(userId, "보낸유저");
            User toUser = userOf(2L, "요청받은유저");
            FriendRequest friendRequest = friendRequestOf(10L, fromUser, toUser);

            given(friendRequestRepository.findAllByFromUser_UserIdAndStatusOrderByCreatedAtDesc(
                    userId,
                    FriendRequestStatus.PENDING
            )).willReturn(List.of(friendRequest));

            // when
            List<FriendRequestResponse> response = friendService.getSentFriendRequests(userId);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).friendRequestId()).isEqualTo(10L);
            assertThat(response.get(0).fromUserId()).isEqualTo(userId);
            assertThat(response.get(0).toUserId()).isEqualTo(2L);
            assertThat(response.get(0).status()).isEqualTo(FriendRequestStatus.PENDING);
            assertThat(response.get(0).nickname()).isEqualTo("요청받은유저");

            then(friendRequestRepository).should()
                    .findAllByFromUser_UserIdAndStatusOrderByCreatedAtDesc(
                            userId,
                            FriendRequestStatus.PENDING
                    );
        }
    }

    private User userOf(Long userId, String nickname) {
        User user = User.builder()
                .email("user" + userId + "@test.com")
                .password("password")
                .nickname(nickname)
                .build();

        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
    }

    private FriendRequest friendRequestOf(Long friendRequestId, User fromUser, User toUser) {
        FriendRequest friendRequest = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();

        ReflectionTestUtils.setField(friendRequest, "friendRequestId", friendRequestId);

        return friendRequest;
    }
}
