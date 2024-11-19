import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/home/HomeView.vue'
import HomePage from "@/views/home/components/HomePage.vue";
import EventPage from "@/views/home/components/EventPage.vue";
import ControlView from "@/views/control/ControlView.vue";
import UserPage from "@/views/control/components/UserPage.vue";
import ControlPage from "@/views/control/components/ControlPage.vue";

import LoginView from "@/views/login/LoginView.vue";
import EventControlPage from "@/views/control/components/EventControlPage.vue";
import ReviewPage from "@/views/control/components/ReviewPage.vue";
import ChatPage from "@/views/home/components/ChatPage.vue";
import MessagePage from "@/views/control/components/MessagePage.vue";
import TagPage from "@/views/control/components/TagPage.vue";
import LevelView from "@/views/home/components/level/LevelView.vue";
import FriendPage from "@/views/home/components/level/components/FriendPage.vue";
import MyEventPage from "@/views/home/components/level/components/MyEventPage.vue";
import LevelPage from "@/views/home/components/level/components/LevelPage.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/home',
            name: 'home',
            component: HomeView,
            children: [{
                path: '',
                name: 'homePage',
                component: HomePage
                 },
                {
                    path: '/event',
                    name: 'eventPage',
                    component: EventPage
                }, {
                    path: '/level',
                    name: 'level',
                    component: LevelView,
                    children: [
                        {
                            path: '',
                            name: 'levelPage',
                            component: LevelPage
                        },
                        {
                            path: '/level/friend',
                            name: 'friend',
                            component: FriendPage
                        },
                        {
                            path: '/level/event',
                            name: 'myEvent',
                            component: MyEventPage
                        },
                    ]
                }, {
                    path: '/chat',
                    name: 'chat',
                    component: ChatPage
                }]
        },
        {
            path: '/',
            name: 'login',
            component: LoginView
        },
        {
            path: '/control',
            name: 'control',
            component: ControlView,
            children: [
                {
                    path: '',
                    name: 'controlPage',
                    component: ControlPage,
                },
                {
                    path: '/control/user',
                    name: 'controlUserPage',
                    component: UserPage
                },
                {
                    path: '/control/tag',
                    name: 'controlTagPage',
                    component: TagPage
                },
                {
                    path: '/control/event',
                    name: 'eventControlPage',
                    component: EventControlPage
                },
                {
                    path: '/control/review',
                    name: 'reviewControlPage',
                    component: ReviewPage
                },
                {
                    path: '/control/message',
                    name: 'messageControlPage',
                    component: MessagePage
                }
            ]
        }
    ]
})

export default router
